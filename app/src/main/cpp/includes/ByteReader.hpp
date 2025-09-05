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

    template<typename T>
    T readPrimitive();

public:
    explicit ByteReader(const std::vector<uint8_t>& buf)
        : buffer(buf), position(0) {}
    bool isBigEndian() {
        static auto checkEndian = []() -> bool {
            uint16_t num = 0x0102;
            return reinterpret_cast<uint8_t*>(&num)[0] == 0x01;
        };
        return checkEndian();
    }

    size_t getPosition() const;
    void setPosition(size_t pos);

    std::byte readByte();
    short readShort();
    unsigned short readUnsignedShort();
    int readInt();
    std::string readString(size_t length);
};

#endif