cmake_minimum_required(VERSION 3.9)
project(leveldb-jni)

# This project requires C++11.
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

if(CMAKE_CXX_COMPILER_ID STREQUAL "MSVC")
  # Disable C++ exceptions.
  string(REGEX REPLACE "/EH[a-z]+" "" CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} /EHs-c-")
  add_definitions(-D_HAS_EXCEPTIONS=0)

  # Disable RTTI.
  string(REGEX REPLACE "/GR" "" CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} /GR-")
else(CMAKE_CXX_COMPILER_ID STREQUAL "MSVC")
  # Enable strict prototype warnings for C code in clang and gcc.
  if(NOT CMAKE_C_FLAGS MATCHES "-Wstrict-prototypes")
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wstrict-prototypes")
  endif(NOT CMAKE_C_FLAGS MATCHES "-Wstrict-prototypes")

  # Disable C++ exceptions.
  string(REGEX REPLACE "-fexceptions" "" CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-exceptions")

  # Disable RTTI.
  string(REGEX REPLACE "-frtti" "" CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-rtti")
endif(CMAKE_CXX_COMPILER_ID STREQUAL "MSVC")

add_library(leveldb-jni SHARED "DB.cpp" "DBIterator.cpp" "WriteBatch.cpp")

if(NOT ANDROID)
  target_include_directories(leveldb-jni PRIVATE "$ENV{JAVA_HOME}/include")
endif()
if(WIN32)
  target_include_directories(leveldb-jni PRIVATE "$ENV{JAVA_HOME}/include/win32")
endif()

target_include_directories(leveldb-jni PRIVATE "./include")
target_link_directories(leveldb-jni PRIVATE "./lib")

if(ANDROID)
  set(ZLIB_NAME z)
else(ANDROID)
  set(ZLIB_NAME zlibstatic)
endif(ANDROID)

target_link_libraries(leveldb-jni ${ZLIB_NAME})
target_link_libraries(leveldb-jni leveldb)
