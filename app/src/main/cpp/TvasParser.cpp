#include "includes/TvasParser.hpp"
#include "includes/Base64Decoder.hpp"
#include "includes/TvasModel.hpp"
#include "includes/TvasCoordinateConverter.hpp"

#include <optional>
#include <iostream>

TileList TvasParser::parseTileSection(ByteReader& reader) {
    TileHeader header;
    header.totalCount = reader.readShort();
    header.infoIndexType = reader.readByte();
    header.reserved = reader.readByte();
    header.infoId = reader.readString(4);
    std::vector<TileData> tiles;
    for (int i = 0; i < header.totalCount; ++i) {
        TileData tile{};
        tile.tileCode = reader.readInt();
        tile.lastVertexIndex = reader.readUnsignedShort();
        tile.reserved = reader.readShort();
        tiles.push_back(tile);
    }
    return TileList{header, tiles};
}

VertexList TvasParser::parseVertexSection(ByteReader& reader) {
    VertexHeader header;
    header.totalCount = reader.readShort();
    header.infoIndexType = reader.readByte();
    header.reserved = reader.readByte();
    header.infoId = reader.readString(4);
    std::vector<VertexData> vertices;
    for (int i = 0; i < header.totalCount; ++i) {
        VertexData vertex{};
        vertex.x = reader.readUnsignedShort();
        vertex.y = reader.readUnsignedShort();
        vertex.distanceBetweenVertex = reader.readShort();
        vertex.timeBetweenVertex = reader.readShort();
        vertices.push_back(vertex);
    }
    return VertexList{header, vertices};
}

TvasHeader TvasParser::parseHeader(ByteReader& reader) {
    TvasHeader header;
    header.totalSize = reader.readInt();
    header.tvasVersion.tvasFormatVersion = reader.readByte();
    header.tvasVersion.reserved = reader.readByte();
    header.tvasVersion.language = reader.readByte();
    header.tvasVersion.charset = reader.readByte();
    header.tvasVersion.mapVersion = reader.readString(8);

    header.routeSearchInfo.routePlanOptionCode = reader.readByte();
    header.routeSearchInfo.fareWeightOptionCode = reader.readByte();
    header.routeSearchInfo.departDayType = reader.readByte();
    header.routeSearchInfo.nextDayType = reader.readByte();
    header.routeSearchInfo.totalDistance = reader.readInt();
    header.routeSearchInfo.totalTime = reader.readInt();
    header.routeSearchInfo.taxiFare = reader.readInt();
    header.routeSearchInfo.controlRouteFlag = reader.readByte();
    header.routeSearchInfo.routeType = reader.readByte();
    header.routeSearchInfo.alternativeBranchPoint = reader.readShort();
    header.routeSearchInfo.destinationLocationInfo = reader.readByte();
    header.routeSearchInfo.routeChangeFlag = reader.readByte();
    header.routeSearchInfo.evRangeReachable = reader.readByte();
    header.routeSearchInfo.reserved = reader.readByte();

    header.mapInfo.coordinateSystem = reader.readByte();
    header.mapInfo.tiles = reader.readByte();
    header.mapInfo.reserved3 = reader.readShort();
    header.mapInfo.departY = reader.readInt();
    header.mapInfo.departX = reader.readInt();
    header.mapInfo.destX = reader.readInt();
    header.mapInfo.destY = reader.readInt();
    header.mapInfo.departName = reader.readString(100);
    header.mapInfo.destName = reader.readString(100);
    header.mapInfo.totalInfoIndexCount = reader.readShort();
    header.mapInfo.reserved4 = reader.readShort();

    for (int i = 0; i < header.mapInfo.totalInfoIndexCount; ++i) {
        InfoIndex infoIndex;
        infoIndex.id = reader.readString(4);
        infoIndex.offset = reader.readInt();
        infoIndex.size = reader.readInt();
        header.infoIndexes.push_back(infoIndex);
    }
    return header;
}

std::optional<InfoIndex> TvasParser::findInfoIndex(const TvasHeader& header, const std::string& idStr) {
    auto it = std::find_if(header.infoIndexes.begin(), header.infoIndexes.end(),
                           [&](const InfoIndex& idx) { return idx.id == idStr; });
    if (it != header.infoIndexes.end()) {
        return *it;
    }
    return std::nullopt;
}

Tvas TvasParser::parseTvas(const std::vector<uint8_t>& bytes) {
    if (bytes.empty()) {
        throw std::invalid_argument("빈 바이트 배열 입력");
    }

    ByteReader reader(bytes);

    try {
        TvasHeader header = parseHeader(reader);
        size_t headerEndPosition = reader.getPosition();

        std::optional<InfoIndex> tileInfoIndex = findInfoIndex(header, "TI");
        std::optional<InfoIndex> vertexInfoIndex = findInfoIndex(header, "VX");

        TileList tileInfo;
        if (tileInfoIndex.has_value()) {
            reader.setPosition(headerEndPosition + tileInfoIndex->offset);
            tileInfo = parseTileSection(reader);
            std::cout << "타일 섹션 파싱 성공" << std::endl;
        } else {
            std::cout << "타일 섹션 없음" << std::endl;
        }

        VertexList vertexInfo;
        if (vertexInfoIndex.has_value()) {
            reader.setPosition(headerEndPosition + vertexInfoIndex->offset);
            vertexInfo = parseVertexSection(reader);
            std::cout << "보간점 섹션 파싱 성공" << std::endl;
        } else {
            std::cout << "보간점 섹션 없음" << std::endl;
        }

        std::cout << "헤더 파싱 성공. 헤더 종료 위치: " << headerEndPosition << std::endl;
        return Tvas{ header, tileInfo, vertexInfo };
    } catch (const std::exception& e) {
        std::cerr << "TvasParser 파싱 중 예외 발생: " << e.what() << std::endl;
        throw;
    }
}

std::vector<GpsPoint> TvasParser::parse(const std::string& input) {
    std::vector<uint8_t> tvasBytes = Base64Decoder::decodeBase64(input);
    Tvas tvas = parseTvas(tvasBytes);
    std::vector<VSMMapPoint> wgsPoints = TvasCoordinateConverter::convert(tvas);
    std::cout << tvas.detailInfo() << std::endl;

    std::vector<GpsPoint> gpsPoints;
    gpsPoints.reserve(wgsPoints.size());
    long currentTimestamp = static_cast<long>(
            std::chrono::duration_cast<std::chrono::milliseconds>(
                    std::chrono::system_clock::now().time_since_epoch()
            ).count()
    );
    for (const auto& wgsPoint : wgsPoints) {
        gpsPoints.emplace_back(wgsPoint.toGpsPoint(currentTimestamp));
    }
    return gpsPoints;
}