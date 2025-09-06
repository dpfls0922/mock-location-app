#ifndef TVAS_MODEL_HPP
# define TVAS_MODEL_HPP

#include <string>
#include <vector>
#include <sstream>
#include <iomanip>

struct TvasVersion{
    std::byte tvasFormatVersion;
    std::byte reserved;
    std::byte language;
    std::byte charset;
    std::string mapVersion;
};

struct RouteSearchInfo {
    std::byte routePlanOptionCode;
    std::byte fareWeightOptionCode;
    std::byte departDayType;
    std::byte nextDayType;
    int totalDistance;
    int totalTime;
    int taxiFare;
    std::byte controlRouteFlag;
    std::byte routeType;
    short alternativeBranchPoint;
    std::byte destinationLocationInfo;
    std::byte routeChangeFlag;
    std::byte evRangeReachable;
    std::byte reserved;
};

struct MapInfo {
    std::byte coordinateSystem;
    std::byte tiles;
    short reserved3;
    int departX;
    int departY;
    int destX;
    int destY;
    std::string departName;
    std::string destName;
    short totalInfoIndexCount;
    short reserved4;
};

struct InfoIndex {
    std::string id;
    int offset;
    int size;
};

struct TvasHeader {
    int totalSize;
    TvasVersion tvasVersion;
    RouteSearchInfo routeSearchInfo;
    MapInfo mapInfo;
    std::vector<InfoIndex> infoIndexes;
};

struct TileHeader {
    short totalCount;
    std::byte infoIndexType;
    std::byte reserved;
    std::string infoId;
};

struct TileData {
    int tileCode;
    size_t lastVertexIndex;
    short reserved;
};

struct TileList {
    TileHeader tileHeader;
    std::vector<TileData> tiles;
};

struct VertexHeader {
    short totalCount;
    std::byte infoIndexType;
    std::byte reserved;
    std::string infoId;
};

struct VertexData {
    int x;
    int y;
    short distanceBetweenVertex;
    short timeBetweenVertex;
};

struct VertexList {
    VertexHeader vertexHeader;
    std::vector<VertexData> vertices;
};

struct Tvas {
    TvasHeader header;
    std::optional<TileList> tileInfo;
    std::optional<VertexList> vertexInfo;

    std::string formatDist(int m) const {
        std::ostringstream str;
        if (m >= 1000) {
            str << std::fixed << std::setprecision(1) << static_cast<double>(m) / 1000.0 << " km";
        } else {
            str << m << " m";
        }
        return str.str();
    }

    std::string formatTime(int sec) const {
        if (sec < 0) return std::to_string(sec) + "s";
        int m = sec / 60;
        int s = sec % 60;
        if (m > 0) return std::to_string(m) + "분 " + std::to_string(s) + "초";
        return std::to_string(s) + "초";
    }

    std::string detailInfo() const {
        std::ostringstream log;

        int totalVertices = vertexInfo ? static_cast<int>(vertexInfo->vertices.size()) : 0;
        int totalTiles = tileInfo ? static_cast<int>(tileInfo->tiles.size()) : 0;

        log << "-------------------- 경로 기본 정보 ---------------------\n";
        log << " 포맷 버전: " << static_cast<int>(header.tvasVersion.tvasFormatVersion) << "\n";
        log << " 언어 코드: " << static_cast<int>(header.tvasVersion.language) << " (0=한국어)\n";
        log << " 문자 인코딩: " << static_cast<int>(header.tvasVersion.charset) << " (0=EUC-KR, 1=UTF-8)\n";
        log << " 지도 버전: " << header.tvasVersion.mapVersion << "\n";
        log << " 총 거리: " << formatDist(header.routeSearchInfo.totalDistance) << "\n";
        log << " 총 시간: " << formatTime(header.routeSearchInfo.totalTime) << "\n";
        log << " 예상 택시 요금: " << header.routeSearchInfo.taxiFare << " 원\n";

        log << " 출발지 " << "(좌표: " << header.mapInfo.departX << ", " << header.mapInfo.departY << ")\n";
        log << " 도착지 " << "(좌표: " << header.mapInfo.destX << ", " << header.mapInfo.destY << ")\n";

        log << "----------------- 데이터 인덱스 목록 (" << header.infoIndexes.size() << "개) -----------------\n";
        for (size_t i = 0; i < std::min(size_t(2), header.infoIndexes.size()); i++) {
            const auto& idx = header.infoIndexes[i];
            log << " " << (i + 1) << ". ID: '" << idx.id << "' | 오프셋: " << idx.offset << " | 크기: " << idx.size << "바이트\n";
        }

        log << "------------------ 타일 정보 (TI) - 총 " << totalTiles << "개 -------------------\n";
        if (!tileInfo || tileInfo->tiles.empty()) {
            log << "  타일 데이터 없음\n";
        } else {
            for (size_t i = 0; i < tileInfo->tiles.size(); i++) {
                const auto& t = tileInfo->tiles[i];
                log << " " << (i + 1) << ". 타일코드: " << t.tileCode << " | 마지막 보간점 인덱스: " << t.lastVertexIndex << "\n";
            }
        }

        log << "--------------- 보간점 정보 (VX) - 총 " << totalVertices << "개 ----------------\n";
        if (!vertexInfo || vertexInfo->vertices.empty()) {
            log << "  보간점 없음\n";
        } else {
            for (size_t i = 0; i < vertexInfo->vertices.size(); i++) {
                const auto& v = vertexInfo->vertices[i];
                log << " #" << (i + 1) << " 좌표: (" << v.x << ", " << v.y << ")"
                    << " | 거리: " << formatDist(static_cast<int>(v.distanceBetweenVertex))
                    << " | 시간: " << formatTime(static_cast<int>(v.timeBetweenVertex)) << "\n";
            }
        }

        log << "---------------------------------------------------\n";
        return log.str();
    }
};

#endif