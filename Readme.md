# MailGuard

MailGuard is a Java-based spam detection tool using the Naive Bayes algorithm. It helps classify messages as spam or not spam based on their content.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [License](#license)

## Introduction

MailGuard is a lightweight Java application designed to classify messages as spam or not spam. It utilizes the Naive Bayes algorithm for spam detection. This project aims to provide a simple yet effective tool for identifying spam messages in various applications.

## Features

- Classification of messages as spam or not spam.
- Trainable model based on labeled datasets.
- Adjustable threshold for classification.
- Easy-to-use command-line interface.

## Installation

1. Clone the repository to your local machine.
2. Ensure you have Java installed (version 8 or higher).
3. Compile the Java files using your preferred IDE or command-line compiler.

## Usage

1. Run the Main class using Java.
2. Follow the prompts to input a message or type 'exit' to quit.
3. MailGuard will classify the message as spam or not spam.

## Documentation

To generate documentation for MailGuard, you can use Javadoc. Run the following command in the terminal:

```bash
javadoc -d docs Main.java
