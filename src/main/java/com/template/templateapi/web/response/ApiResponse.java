package com.template.templateapi.web.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Object errors;

    // ---- success ----
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message, HttpStatus code){
        ApiResponse<T> response = new ApiResponse<>(true, message, data, null);

        return new ResponseEntity<>(response, code);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message){
        return  success(data, message, HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data){
        return  success(data, "", HttpStatus.OK);
    }

    // ---- success with cookies ----
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message, HttpStatus code, ResponseCookie cookie){
        ApiResponse<T> response = new ApiResponse<>(true, message, data, null);

        return ResponseEntity.status(code)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    // ---- error ----
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, Object errors, HttpStatus code){
        ApiResponse<T> response = new ApiResponse<>(false, message, null, errors);

        return new ResponseEntity<>(response, code);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String message, Object errors){
        return error(message, errors, HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String message){
        return error(message, null, HttpStatus.BAD_REQUEST);
    }

    // ---- deleted ----
    public static ResponseEntity<Void> deleted(){
        return ResponseEntity.noContent().build();
    }
}
