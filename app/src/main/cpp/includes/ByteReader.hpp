#ifndef BYTEREADER_HPP
# define BYTEREADER_HPP

#include <vector>
#include <string>
#include <cstdint>
#include <algorithm>
#include <cstring>

class ByteReader {
private:
    const std::vector<uint8_t>& buffer;
    size_t position;

    static bool isBigEndian();
    template<typename T>
    T swapBytes(T value);
    template<typename T>
    T readPrimitive();

public:
    explicit ByteReader(const std::vector<uint8_t>& buf) : buffer(buf), position(0) {}
    size_t getPosition() const;
    void setPosition(size_t pos);

    std::byte readByte();
    short readShort();
    unsigned short readUnsignedShort();
    int readInt();
    std::string readString(size_t length);
};

#endif