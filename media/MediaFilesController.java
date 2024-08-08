package com.xuecheng.media.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @description Media asset file management interface
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
 @Api(value = "Media asset file management interface",tags = "Media asset file management interface")
 @RestController
public class MediaFilesController {


  @Autowired
  MediaFileService mediaFileService;


 @ApiOperation("Media resource list query interface")
 @PostMapping("/files")
 public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
  Long companyId = 1232141425L;
  return mediaFileService.queryMediaFiels(companyId,pageParams,queryMediaParamsDto);

 }

 @ApiOperation("upload image")
 @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public UploadFileResultDto upload(@RequestPart("filedata")MultipartFile filedata,
                                  @RequestParam(value= "objectName",required=false) String objectName) throws IOException {

    //Prepare the file information for uploading
     UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
     //Original file name
     uploadFileParamsDto.setFilename(filedata.getOriginalFilename());
     //File size
     uploadFileParamsDto.setFileSize(filedata.getSize());
     //file type
     uploadFileParamsDto.setFileType("001001");
     //Create a temporary file
     File tempFile = File.createTempFile("minio", ".temp");
     filedata.transferTo(tempFile);
     Long companyId = 1232141425L;
    //file path
     String localFilePath = tempFile.getAbsolutePath();

     //Call service to upload pictures
     UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, localFilePath,objectName);

     return uploadFileResultDto;
 }

}
