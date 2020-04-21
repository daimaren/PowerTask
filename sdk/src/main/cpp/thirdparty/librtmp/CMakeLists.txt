set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -O2 -DSYS=posix -DNO_CRYPTO")

add_definitions("
-DGL_GLEXT_PROTOTYPES
-DEGL_EGLEXT_PROTOTYPES
")

# 添加头文件路径
include_directories(include)


# 添加源文件
file(GLOB FILES_LIB_RTMP "src/*.c")

add_library(librtmp SHARED
        ${FILES_LIB_RTMP}
        )

target_link_libraries(librtmp
        log
        android
        z
        )

