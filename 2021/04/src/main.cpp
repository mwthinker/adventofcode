#include <string>
#include <sstream>
#include <fstream>
#include <ostream>
#include <vector>

#include <fmt/core.h>
#include <fmt/ostream.h>
#include <lyra/lyra.hpp>

const std::vector Draw{7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1};

const std::string Data = R"(
	22 13 17 11  0
	 8  2 23  4 24
	21  9 14 16  7
	 6 10  3 18  5
	 1 12 20 15 19

	 3 15  0  2 22
	 9 18 13 17  5
	19  8  7 25 23
	20 11 10 24  4
	14 21 16 12  6

	14 21 17 24  4
	10 16 15  9 19
	18  8 23 26 20
	22 11 13  6  5
	 2  0 12  3  7
)";

constexpr int BoardSize = 5 * 5;

struct Value {
	int nbr;
	bool marked;
};

//	Create a board from numbers.
//
//	Assumes the following format of numbers:
//		22 13 17 11  0
//		8   2 23  4 24
//		21  9 14 16  7
//		6  10  3 18  5
//		1  12 20 15 19
class Board {
public:
	void addNumber(int nbr) {
		values_.push_back(Value{nbr, false});
	}

	bool isMarked(int x, int y) const {
		return values_[y * 5 + x].marked;
	}

	void mark(int nbr) {
		for (auto& value : values_) {
			if (value.nbr == nbr) {
				value.marked = true;
				lastDrawNbr = nbr;
				break;
			}
		}
	}

	bool winner() const {
		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 5; ++j) {
				if (!isMarked(i, j)) {
					break;
				} else if (j == 4) {
					return true;
				}
			}
		}
		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 5; ++j) {
				if (!isMarked(j, i)) {
					break;
				} else if (j == 4) {
					return true;
				}
			}
		}

		return false;
	}



	int calculateWinningNumber() const {
		return sumUnmarkedValues() * lastDrawNbr;
	}

private:
	int sumUnmarkedValues() const {
		int sum = 0;
		for (auto& value : values_) {
			if (!value.marked) {
				sum += value.nbr;
			}
		}
		return sum;
	}

	int lastDrawNbr = 0;
	std::vector<Value> values_;
};

Board calculateWinner(std::vector<Board> boards, std::vector<int> numbers) {
	for (int nbr : Draw) {
		for (auto& board : boards) {
			board.mark(nbr);

			if (board.winner()) {
				return board;
			}
		}
	}
	return {};
}

struct FileContent {
	std::vector<int> firstRow;
	std::vector<int> data;
};

std::vector<int> extractNumbers(const std::string& row) {
	std::vector<int> numbers;

	std::stringstream stream{row};
	int nbr;
	while (stream >> nbr) {
		numbers.push_back(nbr);
	}
	return numbers;
}

FileContent readFromFile(const std::string& filename) {
	FileContent fileContent;

	std::ifstream infile{filename};

	std::string data;
	std::getline(infile, data);

	fileContent.firstRow = extractNumbers(data);
	
	int nbr;
	while (infile >> nbr) {
		fileContent.data.push_back(nbr);
	}

	return fileContent;
}

FileContent readDefaultBoard() {
	FileContent fileContent;
	fileContent.firstRow = Draw;
	fileContent.data = extractNumbers(Data);

	return fileContent;
}

std::vector<Board> extractBoards(const std::vector<int>& numbers) {
	if (numbers.size() % BoardSize != 0) {
		throw std::runtime_error{""};
	}

	int size = static_cast<int>(numbers.size()) / BoardSize;
	std::vector<Board> boards(size);

	for (int i = 0; i < numbers.size(); ++i) {
		boards[i / BoardSize].addNumber(numbers[i]);
	}
	return boards;
}


template <> struct fmt::formatter<lyra::cli> : ostream_formatter {};

int main(int argc, char** argv) {
	std::string datafile;
	bool showHelp = false;

	auto cli
		= lyra::opt(datafile, "filename")
		["-d"]["--datafile"]
		("filename for the problem data") |
		lyra::help(showHelp);

	auto result = cli.parse(lyra::args(argc, argv));
	if (!result) {
		fmt::print(std::cerr, "Error in command line: {}\n", result.message());
		return 1;
	}

	if (showHelp) {
		fmt::print("{}\n", cli);
		return 0;
	}
	
	std::vector<int> draw;
	std::vector<int> data;
	if (datafile.empty()) {
		draw = Draw;
		data = extractNumbers(Data);
	} else {
		try {
			auto fileContent = readFromFile(datafile);
			draw = fileContent.firstRow;
			data = fileContent.data;
		} catch (const std::exception& exception) {
			fmt::print(std::cerr, "Something wrong with the data, error: {}", exception.what());
			return 1;
		}
	}

	std::vector<Board> boards = extractBoards(data);

	Board winner = calculateWinner(boards, draw);

	fmt::print("Answer: {}\n", winner.calculateWinningNumber());
	return 0;
}
