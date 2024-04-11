package softuni.exam.instagraphlite.models.Dto;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class PicturePathDto {
@XmlElement
    private String path;
@NotBlank
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
