# CONCURRENCY AND MULTITHREADING (X_401031)

## Overview

This course provides a comprehensive presentation of the foundations and
programming principles for multicore computing devices.

## Repository Structure

The repository is structured as follows:

- __DOCUMENTATION__: contains the documentation, implementation design
  choices and evaluation of the implemented data structures. Both the
  .odt and .pdf file format are available for each
  manuscript. REPORT1.odt/pdf describes the coarse grained
  data structures, REPORT2.odt/pdf describes the fine grained ones.
  
- __bin__: contains the scripts to compile, execute and compress the
  program.

- __src__: contains the source code and the files relative to the
  implemented data structures.
  
- __build.xml__: contains the xml structure of the project used by ant
  to compile.
  
- __specifications.pdf__: This file describes the specifications of
  how to implement the data structures.

## Instructions

To compile run in this directory (ant and jdk must be installed):
`./bin/build.sh`

Running `./bin/test_data_structures` displays a small man page with
instructions on how to run the different data implemented data
structures and how to set the parameters.

An example that shows how to execute the coarse grained list with 10
threads, 200 number of items and 1000 as worktime is the following:
`./bin/test_data_structures cgl 10 200 1000`
