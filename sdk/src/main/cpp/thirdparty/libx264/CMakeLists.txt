add_definitions("
-DGL_GLEXT_PROTOTYPES
-DEGL_EGLEXT_PROTOTYPES
")

# 添加头文件路径
include_directories(include)

add_library(libx264 STATIC IMPORTED )
set_target_properties(libx264
        PROPERTIES IMPORTED_LOCATION
        lib/libx264.a)

