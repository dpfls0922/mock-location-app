#include "includes/TvasParser.hpp"
#include "includes/Base64Decoder.hpp"
#include "includes/TvasModel.hpp"
#include "includes/TvasSectionParser.hpp"
#include "includes/TvasCoordinateConverter.hpp"

#include <optional>
#include <iostream>

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

    TileSectionParser tileParser;
    VertexSectionParser vertexParser;
    ByteReader reader(bytes);

    try {
        TvasHeader header = parseHeader(reader);
        size_t headerEndPosition = reader.getPosition();

        std::optional<InfoIndex> tileInfoIndex = findInfoIndex(header, to_string(SectionId::TILE));
        std::optional<InfoIndex> vertexInfoIndex = findInfoIndex(header, to_string(SectionId::VERTEX));

        TileList tileInfo;
        if (tileInfoIndex.has_value()) {
            reader.setPosition(headerEndPosition + tileInfoIndex->offset);
            tileInfo = tileParser.parse(reader);
            std::cout << "타일 섹션 파싱 성공" << std::endl;
        } else {
            std::cout << "타일 섹션 없음" << std::endl;
        }

        VertexList vertexInfo;
        if (vertexInfoIndex.has_value()) {
            reader.setPosition(headerEndPosition + vertexInfoIndex->offset);
            vertexInfo = vertexParser.parse(reader);
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
    for (const auto& wgsPoint : wgsPoints) {
        gpsPoints.push_back(wgsPoint.toGpsPoint());
    }
    return gpsPoints;
}