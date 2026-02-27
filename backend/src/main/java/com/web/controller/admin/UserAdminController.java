package com.web.controller.admin;

import com.web.dto.request.user.ChangeStatusRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final IUserService userService;

    @GetMapping
    public ApiResponse<?> getUsers(){
        return ApiResponse.success(userService.getUers());
    }
    
    @PutMapping("/{id}/status")
    public void changeStatus(@PathVariable Long id,@RequestBody ChangeStatusRequest req){
         userService.changeStatus(id, req);
    }
}
