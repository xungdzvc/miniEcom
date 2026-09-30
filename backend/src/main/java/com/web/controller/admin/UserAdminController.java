package com.web.controller.admin;

import com.web.dto.UserAdminEditDTO;
import com.web.dto.request.user.ChangeStatusRequest;
import com.web.dto.request.user.ChangeStaffRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final IUserService userService;

    @GetMapping
    public ApiResponse<?> getUsers() {
        return ApiResponse.success(userService.getUsers());
    }

    @PutMapping("/{id}/status")
    public ApiResponse<?> changeStatus(@PathVariable Long id,@Valid @RequestBody ChangeStatusRequest req) {
        userService.changeStatus(id, req);
        return ApiResponse.success("Cập nhật trạng thái thành công");
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id),"Lấy thông tin người dùng thành công");
    }

    @PutMapping("/{id}")
    public ApiResponse<?>  updateUser(@PathVariable Long id,@Valid @RequestBody UserAdminEditDTO dto) {
        userService.updateUser(id, dto);
        return ApiResponse.success("Cập nhật thành công người dùng");
    }

}
