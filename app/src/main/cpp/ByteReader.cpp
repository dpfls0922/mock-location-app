#include "includes/ByteReader.hpp"
#include <stdexcept>

template<typename T>
T ByteReader::swapBytes(T value) {
    uint8_t* bytePtr = reinterpret_cast<uint8_t*>(&value);
    std::reverse(bytePtr, bytePtr + sizeof(T));
    return value;
}

bool ByteReader::isBigEndian() {
    static auto checkEndian = []() -> bool {
        uint16_t num = 0x0102;
        return reinterpret_cast<uint8_t*>(&num)[0] == 0x01;
    };
    return checkEndian();
}

template<typename T>
T ByteReader::readPrimitive() {
    if (position + sizeof(T) > buffer.size()) {
        throw std::out_of_range("버퍼 범위를 벗어난 데이터 읽기 시도");
    }

    T value;
    std::memcpy(&value, &buffer[position], sizeof(T));
    position += sizeof(T);

    if (isBigEndian()) {
        value = swapBytes(value);
    }
    return value;
}

template int16_t ByteReader::readPrimitive<int16_t>();
template uint16_t ByteReader::readPrimitive<uint16_t>();
template int32_t ByteReader::readPrimitive<int32_t>();

std::byte ByteReader::readByte() {
    if (position >= buffer.size()) {
        throw std::out_of_range("버퍼 범위를 벗어난 byte 읽기 시도");
    }
    return static_cast<std::byte>(buffer[position++]);
}

short ByteReader::readShort() {
    return readPrimitive<int16_t>();
}

unsigned short ByteReader::readUnsignedShort() {
    return readPrimitive<uint16_t>();
}

int ByteReader::readInt() {
    return readPrimitive<int32_t>();
}

std::string ByteReader::readString(size_t length) {
    if (position + length > buffer.size()) {
        throw std::out_of_range("버퍼 범위를 벗어난 string 읽기 시도");
    }

    std::string result(buffer.begin() + position, buffer.begin() + position + length);
    position += length;

    size_t nullPos = result.find('\0');
    if (nullPos != std::string::npos) {
        result.resize(nullPos);
    }
    result.erase(0, result.find_first_not_of(' '));
    result.erase(result.find_last_not_of(' ') + 1);
    return result;
}

size_t ByteReader::getPosition() const {
    return position;
}

void ByteReader::setPosition(size_t pos) {
    if (pos > buffer.size()) {
        throw std::out_of_range("설정하려는 위치가 버퍼의 범위를 벗어남");
    }
    position = pos;
}
