import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/11 15:44
 */
@Api(value = "Course information management interface",tags = "Course Information Management API")
@RestController
public class CourseBaseInfoController {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("Course Pagination Query API")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")//Specify permission identifier; only those with this permission can access this method.
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required=false) QueryCourseParamsDto queryCourseParamsDto) {

        //Current logged-in user
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        //User's organization ID
        Long companyId = null;
        if(StringUtils.isNotEmpty(user.getCompanyId())){
            companyId = Long.parseLong(user.getCompanyId());
        }


        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(companyId,pageParams, queryCourseParamsDto);

        return courseBasePageResult;

    }
    @ApiOperation("Add course")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Inster.class) AddCourseDto addCourseDto){

        //Get the ID of the user's affiliated organization
        Long companyId = 1232141425L;
//        int i = 1/0;
        CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(companyId, addCourseDto);
        return courseBase;
    }

    @ApiOperation("API for querying based on course ID")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
        //Retrieve the current user's identity
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.out.println(principal);
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        System.out.println(user.getUsername());
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        return courseBaseInfo;
    }


    @ApiOperation("Update course")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated(ValidationGroups.Update.class) EditCourseDto editCourseDto){
        //Retrieve the ID of the organization to which the user belongs
        Long companyId = 1232141425L;
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
        return courseBaseInfoDto;
    }



}
