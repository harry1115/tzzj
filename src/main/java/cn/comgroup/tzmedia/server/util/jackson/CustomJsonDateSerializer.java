/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author pcnsh197
 */
public class CustomJsonDateSerializer extends JsonSerializer<Calendar>
{
    @Override
    public void serialize(Calendar aDate, JsonGenerator aJsonGenerator, SerializerProvider aSerializerProvider)
            throws IOException, JsonProcessingException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(aDate);
        aJsonGenerator.writeString(dateString);
    }
    
}
