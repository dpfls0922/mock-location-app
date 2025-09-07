#ifndef BASE64DECODER_HPP
# define BASE64DECODER_HPP

#include <vector>
#include <cstdint>
#include <array>
#include <stdexcept>

class Base64Decoder {
private:
    static constexpr std::array<int, 256> initLookupTable();
    static const std::array<int, 256> lookupTable;
    static int decodeBase64Value(char c);
    static std::vector<uint8_t> decodeQuantum(const std::array<uint8_t, 4>& quantum, size_t validCount);

public:
    static std::vector<uint8_t> decodeBase64(const std::string &encoded);
};

constexpr std::array<int, 256> Base64Decoder::initLookupTable() {
    std::array<int, 256> table{};
    for (size_t i = 0; i < table.size(); ++i) {
        table[i] = -1;
    }

    for (int i = 0; i < 64; ++i) {
        if (i < 26) {
            table['A' + i] = i;
        } else if (i < 52) {
            table['a' + i - 26] = i;
        } else if (i < 62) {
            table['0' + i - 52] = i;
        } else if (i == 62) {
            table['+'] = i;
        } else {
            table['/'] = i;
        }
    }
    return table;
}

#endif