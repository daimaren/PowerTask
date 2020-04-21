# 添加头文件路径
include_directories( source )

# 添加源文件
add_library( h264encoder

             SHARED

             # library
			 source/aw_alloc.c
			 source/aw_data.c
			 source/aw_x264.c )

# 链接静态库
target_link_libraries( h264encoder
						libx264
                       -landroid
                       -llog )

