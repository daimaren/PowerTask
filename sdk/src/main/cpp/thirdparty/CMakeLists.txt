# 设置cmake最低版本
cmake_minimum_required(VERSION 3.4.1)

add_subdirectory(librtmp)
add_subdirectory(libx264)

# 添加ffmpeg库
add_library( ffmpeg SHARED IMPORTED )
set_target_properties( ffmpeg
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libffmpeg.so )

add_library( fdkaac STATIC IMPORTED )
set_target_properties( fdkaac
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libfdk-aac.a )

# SoundTouch库
add_subdirectory(${CMAKE_SOURCE_DIR}/thirdparty/SoundTouch)
