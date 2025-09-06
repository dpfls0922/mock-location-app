#ifndef GPS_DATA_PARSER_HPP
# define GPS_DATA_PARSER_HPP

#include "GpsPoint.hpp"

class IGpsDataParser {
public:
    virtual ~IGpsDataParser() = default;
    virtual std::vector<GpsPoint> parse(const std::string& input) = 0;
};

#endif