#include "includes/TvasCoordinateConverter.hpp"
#include "includes/Log.hpp"

#include <cmath>
#include <chrono>

#undef LOG_TAG
#define LOG_TAG "COORD_CONVERTER"

VSMMapPoint::VSMMapPoint(double lon, double lat) : longitude(lon), latitude(lat) {}
TileOrigin::TileOrigin(double x, double y) : x(x), y(y) {}
SKCoord::SKCoord(double x, double y) : x(x), y(y) {}
SKCoord SKCoord::fromVertex(int x, int y, const TileOrigin& origin) {
    return SKCoord(origin.x * SCALE + x, origin.y * SCALE + y);
}
GpsPoint VSMMapPoint::toGpsPoint(long timestamp) const {
    const std::string PROVIDER_NAME = "gps";
    return GpsPoint{
            PROVIDER_NAME,
            timestamp,
            latitude,
            longitude,
            5.0,5.f,5.f,5.f,5.f,
            5.f,5.f,3,3,3
    };
}

TileOrigin TvasCoordinateConverter::TileUtils::decodeTileOrigin(int tileCode) {
    int xTileCode = tileCode & 0xFFFF;
    int yTileCode = (tileCode >> 16) & 0xFFFF;
    int mesh = xTileCode * 100 + yTileCode;
    return TileOrigin(calcDminX(mesh), calcDminY(mesh));
}

double TvasCoordinateConverter::TileUtils::calcDminX(int mesh) {
    int a = mesh / 1000;
    int c = (mesh % 100) / 10;
    return 122.0 + a + (c - 1) * TILE_SIZE_X_DEGREE;
}

double TvasCoordinateConverter::TileUtils::calcDminY(int mesh) {
    int a = mesh / 1000;
    int b = (mesh - (a * 1000)) / 100;
    int d = mesh % 10;
    return (99.0 + (b - 1) * 2.0) / 3.0 + (d - 1) * TILE_SIZE_Y_DEGREE;
}

VSMMapPoint TvasCoordinateConverter::CoordConverter::skToWGS84(const SKCoord& sk) {
    double lon = sk.x / SCALE;
    double lat = sk.y / SCALE;
    auto [ecefX, ecefY, ecefZ] = geod2ecef(lat, lon);
    auto [wgsX, wgsY, wgsZ] = bessel2wgs(ecefX, ecefY, ecefZ);
    auto [wgsLat, wgsLon] = ecef2geod(wgsX, wgsY, wgsZ);
    return VSMMapPoint(wgsLon, wgsLat);
}

std::tuple<double, double, double> TvasCoordinateConverter::CoordConverter::geod2ecef(double lat, double lon) {
    double latRad = lat * M_PI / 180.0;
    double lonRad = lon * M_PI / 180.0;
    double f = (BESSEL_A - BESSEL_B) / BESSEL_A;
    double sqre = 2 * f - f * f;
    double N = BESSEL_A / sqrt(1 - sqre * std::pow(std::sin(latRad), 2));
    double x = N * std::cos(latRad) * std::cos(lonRad);
    double y = N * std::cos(latRad) * std::sin(lonRad);
    double z = N * (1 - sqre) * std::sin(latRad);
    return {x, y, z};
}

std::tuple<double, double, double> TvasCoordinateConverter::CoordConverter::bessel2wgs(double ecefX, double ecefY, double ecefZ) {
    double wgsX = ecefX + DX;
    double wgsY = ecefY + DY;
    double wgsZ = ecefZ + DZ;
    return {wgsX, wgsY, wgsZ};
}

std::pair<double, double> TvasCoordinateConverter::CoordConverter::ecef2geod(double x, double y, double z) {
    double p = sqrt(x * x + y * y);
    double theta = atan((z * WGS84_A) / (p * WGS84_B));
    double sqrep = (WGS84_A * WGS84_A - WGS84_B * WGS84_B) / (WGS84_B * WGS84_B);
    double f = (WGS84_A - WGS84_B) / WGS84_A;
    double sqre = 2 * f - f * f;
    double latR = atan((z + sqrep * WGS84_B * std::pow(std::sin(theta), 3)) /
                    (p - sqre * WGS84_A * std::pow(std::cos(theta), 3)));
    double lonR = atan2(y, x);
    double lat = latR * 180.0 / M_PI;
    double lon = lonR * 180.0 / M_PI;
    return {lat, lon};
}

std::vector<VSMMapPoint> TvasCoordinateConverter::convert(const Tvas& tvas) {
    std::vector<VSMMapPoint> points;
    try {
        LOGI("TVAS 좌표 변환 시작");

        if (!tvas.tileInfo || !tvas.vertexInfo) {
            LOGE("tileInfo 또는 vertexInfo가 없습니다.");
            return points;
        }

        const auto& tiles = tvas.tileInfo->tiles;
        const auto& vertices = tvas.vertexInfo->vertices;

        size_t vxIndex = 0;
        for (const auto& tile : tiles) {
            TileOrigin tileOrigin = TileUtils::decodeTileOrigin(tile.tileCode);
            while (vxIndex < vertices.size() && vxIndex <= tile.lastVertexIndex) {
                const auto& vertex = vertices[vxIndex];
                SKCoord skCoord = SKCoord::fromVertex(vertex.x, vertex.y, tileOrigin);
                VSMMapPoint wgsPoint = CoordConverter::skToWGS84(skCoord);
                points.push_back(wgsPoint);

                LOGI("Vertex %zu 변환 성공: 위도=%.6f, 경도=%.6f", (vxIndex + 1), wgsPoint.latitude, wgsPoint.longitude);
                vxIndex++;
            }
        }
        LOGI("총 %zu개 좌표 변환 완료", points.size());
    } catch (const std::exception& e) {
        LOGE("좌표 변환 실패: %s", e.what());
        points.clear();
    }
    return points;
}