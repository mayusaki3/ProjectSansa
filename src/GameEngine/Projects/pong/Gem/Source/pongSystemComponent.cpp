
#include <AzCore/Serialization/SerializeContext.h>
#include <AzCore/Serialization/EditContext.h>
#include <AzCore/Serialization/EditContextConstants.inl>

#include "pongSystemComponent.h"

namespace pong
{
    void pongSystemComponent::Reflect(AZ::ReflectContext* context)
    {
        if (AZ::SerializeContext* serialize = azrtti_cast<AZ::SerializeContext*>(context))
        {
            serialize->Class<pongSystemComponent, AZ::Component>()
                ->Version(0)
                ;

            if (AZ::EditContext* ec = serialize->GetEditContext())
            {
                ec->Class<pongSystemComponent>("pong", "[Description of functionality provided by this System Component]")
                    ->ClassElement(AZ::Edit::ClassElements::EditorData, "")
                        ->Attribute(AZ::Edit::Attributes::AppearsInAddComponentMenu, AZ_CRC("System"))
                        ->Attribute(AZ::Edit::Attributes::AutoExpand, true)
                    ;
            }
        }
    }

    void pongSystemComponent::GetProvidedServices(AZ::ComponentDescriptor::DependencyArrayType& provided)
    {
        provided.push_back(AZ_CRC("pongService"));
    }

    void pongSystemComponent::GetIncompatibleServices(AZ::ComponentDescriptor::DependencyArrayType& incompatible)
    {
        incompatible.push_back(AZ_CRC("pongService"));
    }

    void pongSystemComponent::GetRequiredServices([[maybe_unused]] AZ::ComponentDescriptor::DependencyArrayType& required)
    {
    }

    void pongSystemComponent::GetDependentServices([[maybe_unused]] AZ::ComponentDescriptor::DependencyArrayType& dependent)
    {
    }

    pongSystemComponent::pongSystemComponent()
    {
        if (pongInterface::Get() == nullptr)
        {
            pongInterface::Register(this);
        }
    }

    pongSystemComponent::~pongSystemComponent()
    {
        if (pongInterface::Get() == this)
        {
            pongInterface::Unregister(this);
        }
    }

    void pongSystemComponent::Init()
    {
    }

    void pongSystemComponent::Activate()
    {
        pongRequestBus::Handler::BusConnect();
    }

    void pongSystemComponent::Deactivate()
    {
        pongRequestBus::Handler::BusDisconnect();
    }
}
