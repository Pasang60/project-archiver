/**
 * Author: Pasang Gelbu Sherpa
 * User:VICTUS
 * Date:5/5/2026
 * Time:7:17 PM
 */

package com.pasang.projectarchiver.utils.file.dto;

import com.pasang.projectarchiver.utils.file.FileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileSaveResponse {
    private String fileName;
    private String fileDownloadUri;
    private FileType fileType;
    private String fileDimension;
}
