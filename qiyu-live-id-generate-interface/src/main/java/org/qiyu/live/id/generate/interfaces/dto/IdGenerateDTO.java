package org.qiyu.live.id.generate.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdGenerateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 156456L;

    private Integer bizId;
    private Long generatedId;

}
