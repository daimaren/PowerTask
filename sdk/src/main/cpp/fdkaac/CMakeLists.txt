# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

include_directories(encoder)
include_directories(decoder)

file(GLOB FILES_LIB_COMMON "${PATH_TO_AV_ENGINE}/*.cpp")
file(GLOB FILES_LIB_ENCODER "${PATH_TO_AV_ENGINE}/encoder/*.cpp")
file(GLOB FILES_LIB_DECODER "${PATH_TO_AV_ENGINE}/decoder/*.cpp")

add_library(aac SHARED
        decoder/AACDecoder.cpp
        encoder/AACEncoder.cpp
        AACDecoderJNI.cpp
        AACEncoderJNI.cpp
        )

target_link_libraries(aac
                       log
                        fdkaac
                       )