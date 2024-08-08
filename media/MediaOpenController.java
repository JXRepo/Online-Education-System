package com.xuecheng.media.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/21 10:35
 */
@Api(value = "Media asset file management interface", tags = "Media asset file management interface")
@RestController
@RequestMapping("/open")
public class MediaOpenController {

    @Autowired
    MediaFileService mediaFileService;

    @ApiOperation("Preview File")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId) {
        //Query media asset file information
        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);

        if (mediaFiles == null) {
            return RestResponse.validfail("Video not found");
        }
        //Get the video playback address
        String url = mediaFiles.getUrl();
        if (StringUtils.isEmpty(url)) {
            return RestResponse.validfail("This video is being processed");
        }
        return RestResponse.success(mediaFiles.getUrl());
    }


}
