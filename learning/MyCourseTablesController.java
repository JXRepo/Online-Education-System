
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description My Course Schedule Interface
 * @date 2022/10/25 9:40
 */

@Api(value = "My Course Schedule Interface", tags = "My Course Schedule Interface")
@Slf4j
@RestController
public class MyCourseTablesController {

    @Autowired
    MyCourseTablesService myCourseTablesService;


    @ApiOperation("Add Course")
    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId) {

        //Currently logged in user
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user == null){
            XueChengPlusException.cast("please sign in");
        }
        //User ID
        String userId = user.getId();
        //Add Course
        XcChooseCourseDto xcChooseCourseDto = myCourseTablesService.addChooseCourse(userId, courseId);
        return xcChooseCourseDto;
    }

    @ApiOperation("Check your study qualifications")
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public XcCourseTablesDto getLearnstatus(@PathVariable("courseId") Long courseId) {
        //Currently logged in user
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user == null){
            XueChengPlusException.cast("please sign in");
        }
        //User ID
        String userId = user.getId();

        XcCourseTablesDto learningStatus = myCourseTablesService.getLearningStatus(userId, courseId);

        return learningStatus;

    }

    @ApiOperation("My Class Schedule")
    @GetMapping("/mycoursetable")
    public PageResult<XcCourseTables> mycoursetable(MyCourseTableParams params) {
        //Currently logged in user
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user == null){
            XueChengPlusException.cast("please sign in");
        }
        //User ID
        String userId = user.getId();
        params.setUserId(userId);

        PageResult<XcCourseTables> mycoursetables = myCourseTablesService.mycoursetables(params);
        return mycoursetables;
    }

}
