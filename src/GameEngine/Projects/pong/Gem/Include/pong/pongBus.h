
#pragma once

#include <AzCore/EBus/EBus.h>
#include <AzCore/Interface/Interface.h>

namespace pong
{
    class pongRequests
    {
    public:
        AZ_RTTI(pongRequests, "{0a16afa0-f673-46a4-9b24-0c22e48e4dd4}");
        virtual ~pongRequests() = default;
        // Put your public methods here
    };

    class pongBusTraits
        : public AZ::EBusTraits
    {
    public:
        //////////////////////////////////////////////////////////////////////////
        // EBusTraits overrides
        static constexpr AZ::EBusHandlerPolicy HandlerPolicy = AZ::EBusHandlerPolicy::Single;
        static constexpr AZ::EBusAddressPolicy AddressPolicy = AZ::EBusAddressPolicy::Single;
        //////////////////////////////////////////////////////////////////////////
    };

    using pongRequestBus = AZ::EBus<pongRequests, pongBusTraits>;
    using pongInterface = AZ::Interface<pongRequests>;

} // namespace pong
