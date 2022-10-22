cmake_minimum_required(VERSION 3.24)
project(adventofcode2021problem04
	DESCRIPTION
		"Solves problem 4"
	LANGUAGES
		CXX
)

find_package(fmt CONFIG REQUIRED)
find_package(lyra CONFIG REQUIRED)

add_executable(adventofcode2021problem04
	src/main.cpp
	vcpkg.json
	CMakePresets.json
)

file(COPY ${adventofcode2021problem04_SOURCE_DIR}/data DESTINATION ${CMAKE_CURRENT_BINARY_DIR}) 

if (MSVC)
	target_compile_options(adventofcode2021problem04
		PRIVATE
			/W3 /WX /permissive-
			"/wd26451"
	)
else()
	target_compile_options(adventofcode2021problem04
		PRIVATE
			-Wall -Wextra -Wnon-virtual-dtor -pedantic -Wcast-align -Woverloaded-virtual -Wno-unused-parameter -Wno-parentheses
	)
endif()


target_link_libraries(adventofcode2021problem04
	PRIVATE
		fmt::fmt
		bfg::lyra
)

set_target_properties(adventofcode2021problem04
	PROPERTIES
		CXX_STANDARD 20
		CXX_STANDARD_REQUIRED YES
		CXX_EXTENSIONS NO
)
