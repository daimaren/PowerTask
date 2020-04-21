# 设置cmake最低版本
cmake_minimum_required(VERSION 3.4.1)
include_directories(include)
# 第三方库
add_subdirectory(${CMAKE_SOURCE_DIR}/thirdparty)
# 基于X264封装的H264Enocder库
#add_subdirectory(${CMAKE_SOURCE_DIR}/libH264Enocder)
# 基于librtmp封装的RTMP库
#add_subdirectory(${CMAKE_SOURCE_DIR}/libRTMP)
# 基于FFmepg封装的播放器
#add_subdirectory(${CMAKE_SOURCE_DIR}/MediaPlayer)
# 视频特效库, 贴纸或者实现灵魂出窍，井格，九宫格等
#add_subdirectory(${CMAKE_SOURCE_DIR}/GLFilter)
# 基于libfdk-aac封装的aac编解码库
#add_subdirectory(${CMAKE_SOURCE_DIR}/fdkaac)