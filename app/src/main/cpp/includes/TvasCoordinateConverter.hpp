#ifndef TVAS_COORDINATE_CONVERTER_HPP
# define TVAS_COORDINATE_CONVERTER_HPP

#include <vector>
#include "TvasModel.hpp"
#include "GpsPoint.hpp"

struct VSMMapPoint {
    double longitude;
    double latitude;
    VSMMapPoint(double lon, double lat);
    GpsPoint toGpsPoint(long timestamp) const;
};

struct TileOrigin {
    double x;
    double y;
    TileOrigin(double x = 0.0, double y = 0.0);
};

struct SKCoord {
    static constexpr double SCALE = 360000.0;
    double x;
    double y;
    SKCoord(double x = 0.0, double y = 0.0);
    static SKCoord fromVertex(int x, int y, const TileOrigin& origin);
};

class TvasCoordinateConverter  {
public:
    static std::vector<VSMMapPoint> convert(const Tvas& tvas);

private:
    struct TileUtils {
        static constexpr double TILE_SIZE_X_DEGREE = 7.5 / 60.0;
        static constexpr double TILE_SIZE_Y_DEGREE = TILE_SIZE_X_DEGREE * 2.0 / 3.0;

        static TileOrigin decodeTileOrigin(int tileCode);
        static double calcDminX(int mesh);
        static double calcDminY(int mesh);
    };

    struct CoordConverter {
        static constexpr double BESSEL_A = 6377397.155;
        static constexpr double BESSEL_RF = 299.1528128;
        static constexpr double BESSEL_B = BESSEL_A - BESSEL_A / BESSEL_RF;

        static constexpr double WGS84_A = 6378137.0;
        static constexpr double WGS84_RF = 298.257223563;
        static constexpr double WGS84_B = WGS84_A - WGS84_A / WGS84_RF;

        static constexpr double DX = -147.0;
        static constexpr double DY = 506.0;
        static constexpr double DZ = 687.0;

        static constexpr double SCALE = 360000.0;

        static VSMMapPoint skToWGS84(const SKCoord& sk);
        static std::tuple<double, double, double> geod2ecef(double lat, double lon);
        static std::tuple<double, double, double> bessel2wgs(double ecefX, double ecefY, double ecefZ);
        static std::pair<double, double> ecef2geod(double x, double y, double z);
    };
};

#endif