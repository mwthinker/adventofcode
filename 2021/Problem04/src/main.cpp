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
		int index = y * 5 + x;
		if (index >= 0 && index < values_.size()) {
			return values_[index].marked;
		}
		return false;
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

	Value operator()(int x, int y) const {
		int index = y * 5 + x;
		if (index >= 0 && index < values_.size()) {
			return values_[index];
		}
		return {};
	}

	int calculateWinningNumber() const {
		return sumUnmarkedValues() * lastDrawNbr;
	}

	int getDrawnNbrs() {}

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

std::ostream& operator<<(std::ostream& out, const Board& board) {
	for (int i = 0; i < 5; ++i) {
		for (int j = 0; j < 5; ++j) {
			const auto& value = board(j, i);
			out << value.nbr << (value.marked ? "x " : "  ");
		}
		out << '\n';
	}
	return out;
}

struct Winner {
	Board first;
	Board last;
};

Winner calculateWinner(std::vector<Board> boards, std::vector<int> numbers) {
	std::vector<Board> winners;
	
	for (int nbr : numbers) {
		for (auto& board : boards) {
			if (!board.winner()) {
				board.mark(nbr);

				if (board.winner()) {
					winners.push_back(board);
				}
			}
		}
	}
	if (winners.empty()) {
		return {};
	}
	return Winner{
		.first = winners.front(),
		.last = winners.back()
	};
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
	infile.exceptions(std::ifstream::badbit);

	std::string row;
	std::getline(infile, row);
	std::replace(row.begin(), row.end(), ',', ' ');

	fileContent.firstRow = extractNumbers(row);
	
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
		throw std::runtime_error{"Could not extract boards"};
	}

	int size = static_cast<int>(numbers.size()) / BoardSize;
	std::vector<Board> boards(size);

	for (int i = 0; i < numbers.size(); ++i) {
		boards[i / BoardSize].addNumber(numbers[i]);
	}
	return boards;
}

template <> struct fmt::formatter<lyra::cli> : ostream_formatter {};

template <> struct fmt::formatter<Board> : ostream_formatter {};

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
		} catch (const std::ios_base::failure& fail) {
			fmt::print(std::cerr, "Something wrong with the data, error: {}", fail.what());
			return 1;
		}
	}
	try {
		std::vector<Board> boards = extractBoards(data);

		Winner winner = calculateWinner(boards, draw);
		fmt::print("Answer1: {}\n", winner.first.calculateWinningNumber());
		fmt::print("Board: \n{}\n", winner.first);
		fmt::print("Answer2: {}\n", winner.last.calculateWinningNumber());
		fmt::print("Board: \n{}\n", winner.last);
	} catch (const std::exception& exception) {
		fmt::print(std::cerr, "Something wrong with the data, error: {}", exception.what());

		return 1;
	}
	return 0;
}
