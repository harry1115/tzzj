/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.common.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.TIMESTAMP;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;

/**
 *
 * @author pcnsh197
 */
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@ObjectTypeConverters({
    @ObjectTypeConverter(name = "pictureType", objectType = PictureType.class,
            dataType = String.class, conversionValues = {
                @ConversionValue(dataValue = "PORTRAIT", objectValue = "PORTRAIT"),
                @ConversionValue(dataValue = "MAIN", objectValue = "MAIN"),
                @ConversionValue(dataValue = "SUBSIDIARY", objectValue = "SUBSIDIARY"),
                @ConversionValue(dataValue = "SUBTHUMB", objectValue = "SUBTHUMB")})
})
public abstract class TZImage implements Serializable {
    
    @Basic
    @Column(name = "IMAGENAME")
    private String imageName;
    
    @Id  
    @Column(name = "FILEPATH", updatable = false)
    private String filePath;

    @Basic
    @Column(name = "PICTURETYPE")
    @Convert("pictureType")
    private PictureType pictureType = PictureType.PORTRAIT;
    
    
    @Temporal(TIMESTAMP)
    @Column(name = "CREATIONDATETIME")
    private Calendar creationDateTime;
    
    public TZImage() {
        creationDateTime = Calendar.getInstance();
        creationDateTime.setTime(new Date());
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public PictureType getPictureType() {
        return pictureType;
    }

    public void setPictureType(PictureType pictureType) {
        this.pictureType = pictureType;
    }

    public Calendar getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Calendar creationDateTime) {
        this.creationDateTime = creationDateTime;
    }
    
    
    
}
