package cn.comgroup.tzmedia.server.util.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 *
 * This class is used to load the certificate from the smtps server to Glassfish
 * keystore,after run this then the send mail var SSL can be successful.
 *
 *
 * How to use this class args[0]="10.62.120.139:465"; is the mail server and
 * port; args[1]="changeit"; is the password of the keystore, these 2 argument
 * should be not changed.
 *
 *
 * What we need to change is the keystore path of the Glassfish,in the code
 * should be
 * keyStorePath="/home/pcnsh197/work/glassfish-4.0/glassfish/domains/domain1/config";
 *
 *
 *
 *
 * @author pcnsh197
 */
public class InstallCert {

    public static void main(String[] args) throws Exception {
        String host;
        int port;
        char[] passphrase;
//        args = new String[2];
//        args[0] = "10.62.120.139:465";
//        args[1] = "changeit";

//        args[0] = "sandboxapp.cloopen.com:8883";
//        args[1] = "changeit";
        //Change this path according to your Glassfish environment.
//        String keyStorePath="/home/pcnsh197/work/glassfish-4.0/glassfish/domains/domain1/config";
        String keyStorePath = args[2];

        if (args.length > 0) {
            String[] c = args[0].split(":");
            host = c[0];
//            port = (c.length == 1) ? 443 : Integer.parseInt(c[1]);
            port = (c.length == 1) ? 8883 : Integer.parseInt(c[1]);
            String p = (args.length == 1) ? "changeit" : args[1];
            passphrase = p.toCharArray();
        } else {
            System.out.println("Usage: java InstallCert <host>[:port] [passphrase] [keystorepath]");
            return;
        }

        File file = new File("cacerts.jks");
        if (file.isFile() == false) {
            File dir = new File(keyStorePath);
            file = new File(dir, "cacerts.jks");
            if (file.isFile() == false) {
                file = new File(dir, "cacerts.jks");
            }
        }
        System.out.println("Loading KeyStore " + file + "...");
        InputStream in = new FileInputStream(file);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(in, passphrase);
        in.close();

        SSLContext context = SSLContext.getInstance("TLS");
        TrustManagerFactory tmf
                = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory factory = context.getSocketFactory();

        System.out.println("Opening connection to " + host + ":" + port + "...");
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        socket.setSoTimeout(10000);
        try {
            System.out.println("Starting SSL handshake...");
            socket.startHandshake();
            socket.close();
            System.out.println();
            System.out.println("No errors, certificate is already trusted");
        } catch (SSLException e) {
            System.out.println();
            e.printStackTrace(System.out);
        }

        X509Certificate[] chain = tm.chain;
        if (chain == null) {
            System.out.println("Could not obtain server certificate chain");
            return;
        }

        BufferedReader reader
                = new BufferedReader(new InputStreamReader(System.in));

        System.out.println();
        System.out.println("Server sent " + chain.length + " certificate(s):");
        System.out.println();
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        for (int i = 0; i < chain.length; i++) {
            X509Certificate cert = chain[i];
            System.out.println(" " + (i + 1) + " Subject " + cert.getSubjectDN());
            System.out.println("   Issuer  " + cert.getIssuerDN());
            sha1.update(cert.getEncoded());
            System.out.println("   sha1    " + toHexString(sha1.digest()));
            md5.update(cert.getEncoded());
            System.out.println("   md5     " + toHexString(md5.digest()));
            System.out.println();
        }

        System.out.println("Enter certificate to add to trusted keystore or 'q' to quit: [1]");
        String line = reader.readLine().trim();
        int k;
        try {
            k = (line.length() == 0) ? 0 : Integer.parseInt(line) - 1;
        } catch (NumberFormatException e) {
            System.out.println("KeyStore not changed");
            return;
        }

        X509Certificate cert = chain[k];
        String alias = host + "-" + (k + 1);
        ks.setCertificateEntry(alias, cert);

        OutputStream out = new FileOutputStream(keyStorePath + "/cacerts.jks");
        ks.store(out, passphrase);
        out.close();

        System.out.println();
        System.out.println(cert);
        System.out.println();
        System.out.println("Added certificate to keystore 'jssecacerts' using alias '"
                + alias + "'");
    }

    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }

    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }

}
