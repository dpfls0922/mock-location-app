#ifndef GPS_POINT_HPP
# define GPS_POINT_HPP

#include <string>

struct GpsPoint {
    std::string provider;
    long timestamp;
    double latitude;
    double longitude;
    double altitude;
    float hAcc;
    float vAcc;
    float sAcc;
    float speed;
    float bearing;
    float bAcc;
    int satellites;
    int maxCn0;
    int meanCn0;
};

#endif
