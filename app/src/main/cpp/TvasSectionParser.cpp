#include "includes/ByteReader.hpp"
#include "includes/TvasSectionParser.hpp"

SectionId TileSectionParser::getId() const {
    return SectionId::TILE;
}

TileList TileSectionParser::parse(ByteReader& reader) {
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

SectionId VertexSectionParser::getId() const {
    return SectionId::VERTEX;
}

VertexList VertexSectionParser::parse(ByteReader& reader) {
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