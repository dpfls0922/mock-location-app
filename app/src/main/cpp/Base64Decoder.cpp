#include "includes/Base64Decoder.hpp"

const std::array<int, 256> Base64Decoder::lookupTable = Base64Decoder::initLookupTable();

// 문자 c에 대응되는 6비트 값
int Base64Decoder::decodeBase64Value(char c) {
    int val = lookupTable[static_cast<unsigned char>(c)];
    if (val == -1) {
        throw std::invalid_argument("유효하지 않은 Base64 문자입니다");
    }
    return val;
}

// 6비트의 문자 4개와 유효 개수를 받아서 8비트의 (최대) 3개로 만드는 과정
std::vector<uint8_t> Base64Decoder::decodeQuantum(const std::array<uint8_t, 4>& quantum, size_t quantumIndex) {
    std::vector<uint8_t> result;

    result.push_back((quantum[0] << 2) | ((quantum[1] & 0x30) >> 4));
    if (quantumIndex > 2) {
        result.push_back(((quantum[1] & 0x0F) << 4) | ((quantum[2] & 0x3C) >> 2));
    }
    if (quantumIndex > 3) {
        result.push_back(((quantum[2] & 0x03) << 6) | quantum[3]);
    }
    return result;
}

std::vector<uint8_t> Base64Decoder::decodeBase64(const std::string& encoded) {
    std::vector<uint8_t> result;
    size_t inputIndex = 0;
    size_t inputLength = encoded.size();

    while (inputIndex < inputLength) {
        size_t quantumIndex = 0;
        std::array<uint8_t, 4> quantum{};

        while (quantumIndex < 4 && inputIndex < inputLength) {
            char currentChar = encoded[inputIndex++];
            if (std::isspace(currentChar) || std::isspace(currentChar)) {
                continue;
            }
            quantum[quantumIndex++] = decodeBase64Value(currentChar);
        }

        if (quantumIndex == 0) {
            break;
        }

        std::vector<uint8_t> decodedQuantum = decodeQuantum(quantum, quantumIndex);
        result.insert(result.end(), decodedQuantum.begin(), decodedQuantum.end());
    }
    return result;
}