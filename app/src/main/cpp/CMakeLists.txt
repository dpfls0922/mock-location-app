cmake_minimum_required(VERSION 3.10.2)

project("mocklocationapp")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

include_directories(
        ${CMAKE_SOURCE_DIR}/src/main/cpp
        ${CMAKE_SOURCE_DIR}/src/main/cpp/includes
)

file(GLOB CPP_FILES
        "${CMAKE_SOURCE_DIR}/*.cpp"
)

add_library(
        tvas_parser
        SHARED
        ${CPP_FILES}
)

find_library(
        log-lib
        log
)

if(NOT log-lib)
    message(WARNING "Could not find log library; continuing without linking log-lib")
    set(log-lib "")
endif()

target_link_libraries(
        tvas_parser
        ${log-lib}
        -lstdc++
)

target_compile_options(tvas_parser PRIVATE -Wall -Wextra -Werror)