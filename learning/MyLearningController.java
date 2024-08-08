package com.xuecheng.learning.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.learning.service.LearningService;
import com.xuecheng.learning.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description My learning interface
 * @date 2022/10/27 8:59
 */
@Api(value = "Learning process management interface", tags = "Learning process management interface")
@Slf4j
@RestController
public class MyLearningController {

    @Autowired
    LearningService learningService;

    /**
     *
     * @param courseId Course ID
     * @param teachplanId Lesson plan id
     * @param mediaId Media asset file id
     * @return
     */
    @ApiOperation("Get Video")
    @GetMapping("/open/learn/getvideo/{courseId}/{teachplanId}/{mediaId}")
    public RestResponse<String> getvideo(@PathVariable("courseId") Long courseId, @PathVariable("teachplanId") Long teachplanId, @PathVariable("mediaId") String mediaId) {

        SecurityUtil.XcUser user = SecurityUtil.getUser();

        String userId = user.getId();

        //Get Video
        RestResponse<String> restResponse = learningService.getVideo(userId, courseId, teachplanId, mediaId);

        return restResponse;

    }

}
