#include <iostream>
#include <string>
#include <vector>
#include <cassert>
#include "includes/Base64Decoder.hpp"
#include "includes/ByteReader.hpp"

void testReadInt() {
    std::vector<uint8_t> data = {0x01, 0x02, 0x03, 0x04};
    ByteReader reader(data);

    int32_t value = reader.readInt();
    int32_t expected = 0x04030201;

    assert(value == expected);
}

void testReadString() {
    std::vector<uint8_t> data = {'h', 'e', 'l', 'l', 'o', '\0', ' ', ' '};
    ByteReader reader(data);

    std::string str1 = reader.readString(5, false);
    assert(str1 == "hello");

    reader.setPosition(0);
    std::string str2 = reader.readString(8, true);
    assert(str2 == "hello");
}

void testSetPosition() {
    std::vector<uint8_t> data = {0, 1, 2};
    ByteReader reader(data);

    reader.setPosition(2);
    assert(reader.getPosition() == 2);

    try {
        reader.setPosition(4);
        assert(false);
    } catch (const std::out_of_range&) {
    }
}

void testBase64Decode() {
    std::string base64Input = "SGVsbG8gV29ybGQh";

    try {
        auto bytes = Base64Decoder::decodeBase64(base64Input);
        std::string decoded(bytes.begin(), bytes.end());
        assert(decoded == "Hello World!");
    } catch (const std::exception &ex) {
        std::cerr << "디코딩 오류: " << ex.what() << std::endl;
        assert(false);
    }
}

int main() {
    testBase64Decode();

    testReadInt();
    testReadString();
    testSetPosition();
    return 0;
}