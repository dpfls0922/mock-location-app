cmake_minimum_required(VERSION 3.10.2)

project("mocklocationapp")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

include_directories(
        ${CMAKE_SOURCE_DIR}
        ${CMAKE_SOURCE_DIR}/includes
)

include_directories(${JNI_INCLUDE_DIRS})

file(GLOB CPP_FILES "${CMAKE_SOURCE_DIR}/*.cpp")

add_library(
        tvas_parser
        SHARED
        ${CPP_FILES}
)

target_link_libraries(
        tvas_parser
        android
        c++_static
        log
)

target_compile_options(tvas_parser PRIVATE -Wall -Wextra -Werror)