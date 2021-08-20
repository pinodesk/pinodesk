package toscabox.desktop.viewmodel;

import java.util.Date;

import lombok.Data;

@Data
public class LanguageVM {
    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private String code;
    private String name;
}
