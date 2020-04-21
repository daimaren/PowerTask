# 添加头文件路径
include_directories( source )

# 添加源文件
add_library( rtmp

             SHARED

             # library
			 source/aw_array.c
			 source/aw_rtmp.c
			 source/aw_thread_poll.c
			 source/aw_streamer.c )

# 链接静态库
target_link_libraries( rtmp
						librtmp
                       -landroid
                       -llog )

