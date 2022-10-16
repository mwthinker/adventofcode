#include <string>
#include <sstream>
#include <string_view>
#include <vector>
#include <iostream>

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

struct Value {
	int nbr;
	bool marked;
};

class Board {
public:
	Board() {

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

int main() {
	std::stringstream stream(Data);

	std::vector<Board> boards{1};

	int value = 0;
	int index = 0;
	while (stream >> value) {
		if (index == boards.size()) {
			boards.emplace_back();
		}

		boards[index].values_.push_back({value, false});
		if (boards[index].values_.size() == 25) {
			++index;
		}
	}

	Board winner = calculateWinner(boards, Draw);

	std::cout << "Answer: " << winner.sumUnmarkedValues() * winner.lastDrawNbr << "\n";
	return 0;
}
