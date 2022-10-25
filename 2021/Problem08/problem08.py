#!/usr/bin/env python3

import argparse

def run(args):
    filename = args.filename
    if filename is None:
        print("Missing file")
        exit(1)

def main():
    parser = argparse.ArgumentParser(description="Solves problem 07 2021")
    parser.add_argument(
        "-f", "--datafile", help="the file containing the data", dest="filename")
    parser.set_defaults(func=run)
    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
