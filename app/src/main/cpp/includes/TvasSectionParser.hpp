#ifndef TVAS_SECTION_PARSER
# define TVAS_SECTION_PARSER

#include <string>
#include "TvasModel.hpp"

enum class SectionId {
    TILE,
    VERTEX
};

inline std::string to_string(SectionId id) {
    switch (id) {
        case SectionId::TILE: return "TI";
        case SectionId::VERTEX: return "VX";
        default: return "";
    }
}

template<typename T>
class SectionParser {
private:
    SectionId id;
public:
    virtual ~SectionParser() = default;
    virtual SectionId getId() const = 0;
    virtual T parse(class ByteReader& reader) = 0;
};

class TileSectionParser : public SectionParser<TileList> {
public:
    TileSectionParser() = default;
    virtual ~TileSectionParser() = default;
    SectionId getId() const override;
    TileList parse(ByteReader& reader) override;
};

class VertexSectionParser : public SectionParser<VertexList> {
public:
    VertexSectionParser() = default;
    virtual ~VertexSectionParser() = default;
    SectionId getId() const override;
    VertexList parse(ByteReader& reader) override;
};

#endif