#ifndef TVAS_PARSER_HPP
# define TVAS_PARSER_HPP

#include <vector>
#include <string>
#include "IGpsDataParser.hpp"
#include "TvasModel.hpp"
#include "ByteReader.hpp"

class TvasParser : public IGpsDataParser {
private:
    TvasHeader parseHeader(ByteReader& reader);
    std::optional<InfoIndex> findInfoIndex(const TvasHeader& header, const std::string& idStr);
    Tvas parseTvas(const std::vector<uint8_t>& bytes);
public:
    ~TvasParser() = default;
    std::vector<GpsPoint> parse(const std::string& input) override;
};

#endif