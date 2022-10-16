cmake_minimum_required(VERSION 3.24)
project(adventofcode2021problem06
	DESCRIPTION
		"Solves problem 6"
	LANGUAGES
		CXX
)

find_package(fmt CONFIG REQUIRED)

add_executable(adventofcode2021problem06
	src/main.cpp
	vcpkg.json
	CMakePresets.json
)

if (MSVC)
	target_compile_options(adventofcode2021problem06
		PRIVATE
			/W3 /WX /permissive-
			"/wd26451"
	)
else()
	target_compile_options(adventofcode2021problem06
		PRIVATE
			-Wall -Wextra -Wnon-virtual-dtor -pedantic -Wcast-align -Woverloaded-virtual -Wno-unused-parameter -Wno-parentheses
	)
endif()


target_link_libraries(adventofcode2021problem06
	PRIVATE
		fmt::fmt
)

set_target_properties(adventofcode2021problem06
	PROPERTIES
		CXX_STANDARD 20
		CXX_STANDARD_REQUIRED YES
		CXX_EXTENSIONS NO
)
