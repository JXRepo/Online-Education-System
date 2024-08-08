package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description Course plan management related interfaces
 * @date 2023/2/14 11:25
 */
@Api(value = "Course plan editing interface",tags = "Course plan editing interface")
@RestController
public class TeachplanController {

    @Autowired
    TeachplanService teachplanService;

   @ApiOperation("Query the course plan tree structure")
   //Query the course plan  GET /teachplan/22/tree-nodes
   @GetMapping("/teachplan/{courseId}/tree-nodes")
 public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
       List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);

       return teachplanTree;
   }

    @ApiOperation("Create or modify course plan")
    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }

    @ApiOperation(value = "Bind course plan and media information")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

}
