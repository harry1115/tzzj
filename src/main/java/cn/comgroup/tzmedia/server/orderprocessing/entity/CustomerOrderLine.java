package cn.comgroup.tzmedia.server.orderprocessing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "CUSTOMERORDERLINES")
@NamedQueries({
    @NamedQuery(name = "CustomerOrderLine.findLinesByProductNumber", query = "SELECT count(col) FROM CustomerOrderLine col WHERE col.productNumber = :productNumber")
})
public class CustomerOrderLine implements Serializable {
    @Id
    @Column(name = "UNIQUENUMBER", updatable = false)
    @TableGenerator(name = "ORDERLINE_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "ORDERLINE_GEN",
            allocationSize=1,
            initialValue = 1)
    @GeneratedValue(generator = "ORDERLINE_GENERATOR")
    private long uniqueNumber;
    
    @Column(name = "LINENUMBER")
    private int lineNumber;

    @Column(name = "PRODUCTNUMBER")
    private String productNumber;

    @Basic
    @Column(name = "PRODUCTNAME")
    private String productName;

    @Column(name = "PLAYBILLID")
    private int playbillId;

    @Basic
    @Column(name = "PLAYBILLNAME")
    private String playbillName;

    @Basic
    @Column(name = "ORDERQUANTITY")
    private int orderQuantity;

    @Basic
    @Column(name = "PRICE")
    private double price;

    @Basic
    @Column(name = "LINEAMOUNT")
    private double lineAmount;
   
    @Column(name = "SONGID")
    private int songId;    
    
    @Basic
    @Column(name = "SONGNAME")
    private String songName;
    
    @Basic
    @Column(name = "GRABCOMMENT")
    @Lob
    private String grabComment;
    

    @ManyToOne
    @JoinColumn(name = "ORDERNUMBER")
    @JsonIgnore
    private CustomerOrder owner;
    
    public CustomerOrderLine(){
        
    }
    
    public CustomerOrderLine(
            int lineNumber,
            String productNumber,
            String productName,
            int playbillId,
            String playbillName,
            int orderQuantity,
            double price){
        this.productNumber=productNumber;
        this.productName=productName;
        this.playbillId=playbillId;
        this.playbillName=playbillName;
        this.orderQuantity=orderQuantity;
        this.price=price;
        this.lineAmount=orderQuantity*price;
    }

    public long getUniqueNumber() {
        return uniqueNumber;
    }

    public void setUniqueNumber(long uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getPlaybillId() {
        return playbillId;
    }

    public void setPlaybillId(int playbillId) {
        this.playbillId = playbillId;
    }

    public String getPlaybillName() {
        return playbillName;
    }

    public void setPlaybillName(String playbillName) {
        this.playbillName = playbillName;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
        this.lineAmount=orderQuantity*price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        this.lineAmount=orderQuantity*price;
    }

    public double getLineAmount() {
        lineAmount=orderQuantity*price;
        return lineAmount;
    }

    public void setLineAmount(double lineAmount) {
        this.lineAmount = lineAmount;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getGrabComment() {
        return grabComment;
    }

    public void setGrabComment(String grabComment) {
        this.grabComment = grabComment;
    }

    protected CustomerOrder getOwner() {
        return owner;
    }

    protected void setOwner(CustomerOrder owner) {
        this.owner = owner;
    }

    /**
     * Returns a string representation of the object. This implementation
     * constructs that representation based on the id fields.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "CustomerOrderLine:orderNumber " + owner.getOrderNumber()+
                ":lineNumber"+lineNumber;
    }
}
