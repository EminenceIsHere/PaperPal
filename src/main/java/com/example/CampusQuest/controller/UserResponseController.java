package com.example.CampusQuest.controller;

import com.example.CampusQuest.entity.ExamFile;
import com.example.CampusQuest.entity.UserResponse;
import com.example.CampusQuest.service.UserResponseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/userresponse")
public class UserResponseController {

    private final UserResponseService userResponseService;

    public UserResponseController(UserResponseService userResponseService) {
        this.userResponseService = userResponseService;
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity sendData(
            @RequestParam("course") String course,
            @RequestParam("branch") String branch,
            @RequestParam("semester") String semester,
            @RequestParam("fileType") String fileType,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            UserResponse userResponse = new UserResponse();
            userResponse.setCourse(course);
            userResponse.setBranch(branch);
            userResponse.setSemester(Integer.parseInt(semester));
            if (userResponseService.getExamLinkByUserResponse(userResponse) == null) {
                List<ExamFile> examFiles = userResponseService.saveUserResponse(userResponse, file,fileType).getExamFile();
                if (examFiles.size() > 0) {
                    return new ResponseEntity<>(HttpStatus.OK) ;
                } else {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }

            } else {
                userResponseService.addFileToUser(userResponse, file,fileType);
                return new ResponseEntity<>(HttpStatus.OK);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return  new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @GetMapping("/getlinks")
    public String getExamFileLink(
            @RequestParam("course") String course,
            @RequestParam("branch") String branch,
            @RequestParam("semester")int semester,
            Model model
    ) {
        UserResponse userResponse = new UserResponse();
        userResponse.setCourse(course);
        userResponse.setBranch(branch);
        userResponse.setSemester(semester);
        List<ExamFile> examFiles = userResponseService.getExamLinkByUserResponse(userResponse);
        HashMap<String,String> links = new HashMap<>();
        if (examFiles != null) {
            for (ExamFile examFile : examFiles) {
                links.put(examFile.getDownloadLink(),examFile.getContentType());
            }
            model.addAttribute("links", links);
            return "links-exam";
        }
        return "links-exam";
    }
    @PostMapping("/addfile")
    public ResponseEntity addFileToUserResponse(
            @RequestParam("course") String course,
            @RequestParam("branch") String branch,
            @RequestParam("semester") String semester,
            @RequestParam("fileType") String fileType,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        UserResponse userResponse = new UserResponse();
        userResponse.setCourse(course);
        userResponse.setBranch(branch);
        userResponse.setSemester(Integer.parseInt(semester));
        userResponse = userResponseService.addFileToUser(userResponse, file,fileType);
        if (userResponse != null) {
            return new ResponseEntity<>(HttpStatus.OK) ;
        }else{
            return  new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity deleteUserResponse(
            @RequestParam("course") String course,
            @RequestParam("branch") String branch,
            @RequestParam("semester") String semester
    ) throws JsonProcessingException {
        try {
            UserResponse userResponse = new UserResponse();
            userResponse.setCourse(course);
            userResponse.setBranch(branch);
            userResponse.setSemester(Integer.parseInt(semester));
            userResponseService.deleteByUserResponse(userResponse);
            return new ResponseEntity<>(HttpStatus.OK) ;
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
