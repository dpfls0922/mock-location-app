#include <iostream>
#include <string>
#include "includes/Base64Decoder.hpp"

void testBase64Decode() {
    std::string base64Input = "SGVsbG8gV29ybGQh";

    try {
        auto bytes = Base64Decoder::decodeBase64(base64Input);
        std::string decoded(bytes.begin(), bytes.end());
        std::cout << "디코딩된 문자열: " << decoded << std::endl;
    } catch (const std::exception &ex) {
        std::cerr << "디코딩 오류: " << ex.what() << std::endl;
    }
}

int main() {
    testBase64Decode();
    return 0;
}