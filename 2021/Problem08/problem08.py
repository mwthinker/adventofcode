#!/usr/bin/env python3

import argparse

class Code:
    def __init__(self, code: str):
        self._code = "".join(sorted(code))

def run(args):
    filename = args.filename
    if filename is None:
        print("Missing file")
        exit(1)
    file=open(filename, 'r')
    lines=file.readlines()
    
    codes = {
        Code("abcefg", 0): 0,
        Code("cf"): 1,
        Code("acdeg"): 2,
        Code("acdfg"): 3,
        Code("bcdf"): 4,
        Code("abdfg"): 5,
        Code("abdefg"): 6,
        Code("acf"): 7,
        Code("abcdefg"): 8,
        Code("abcfg"): 9
    }

    segments=[]
    for row in lines:
        columns=row.split("|")
        if len(columns) != 2:
            continue
        #columns[0].split(" ")
        columns[1].rstrip().split(" ")
        #segments.append(Segment(code))
        print(row)

def main():
    parser = argparse.ArgumentParser(description="Solves problem 07 2021")
    parser.add_argument(
        "-f", "--datafile", help="the file containing the data", dest="filename")
    parser.set_defaults(func=run)
    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
