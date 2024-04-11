package softuni.exam.instagraphlite.models.Dto;

import com.google.gson.annotations.Expose;

import javax.validation.constraints.*;

public class PictureSeedDto {
@Expose
    private String path;
@Expose
    private double size;

    public PictureSeedDto() {
    }
@NotBlank
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
@Min(500)
@Max(60000)
    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }
}
