package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description Course plan information model class
 * @date 2023/2/14 11:23
 */
@Data
@ToString
public class TeachplanDto extends Teachplan {
  //Information related to media asset management
   private TeachplanMedia teachplanMedia;

  //List of small chapters
   private List<TeachplanDto> teachPlanTreeNodes;
}
